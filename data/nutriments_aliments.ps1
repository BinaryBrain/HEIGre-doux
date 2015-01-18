$csv = Import-Csv .\nutriments_aliments.csv -Delimiter `t -Encoding "utf8" -Header A,B

$query = "insert into nutriments_aliments (id, name_F) values "
$stringBuilder = New-Object System.Text.StringBuilder
$null = $stringBuilder.Append($query)


$csv | foreach {
    $A = $_.A
    $B = $_.B.replace("'", "\'")
    $null = $stringBuilder.Append("(`'$A`', `'$B`'),`r`n")
}

$query = $stringBuilder.ToString()
$query = $query.Substring(0, $query.Length - 3)

$query > .\nutriments_aliments.sql